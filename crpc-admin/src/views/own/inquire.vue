<template>
    <section>
        <!--工具条-->
        <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
            <el-form :inline="true" :model="filters" style="float: left">
                <el-form-item>
                    <el-input v-model="filters.name" placeholder="输入服务名"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" v-on:click="getSearchData">查询</el-button>
                </el-form-item>
                <!--<el-form-item>-->
                    <!--<el-button type="primary" @click="handleAdd">新增</el-button>-->
                <!--</el-form-item>-->
            </el-form>
            <el-form :inline="true" :model="filters" style="float: right">
              <el-form-item>
                <el-button type="primary" v-on:click="provide">在线</el-button>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" v-on:click="consume">全部</el-button>
              </el-form-item>
            </el-form>
        </el-col>

        <!--列表-->
        <el-table :data="DataSource" highlight-current-row v-loading="listLoading" style="width: 100%;">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column type="index" width="60">
            </el-table-column>
            <el-table-column prop="name" label="服务名" sortable>
            </el-table-column>
            <el-table-column prop="group" label="组" :formatter="formatSex" sortable>
            </el-table-column>
            <el-table-column prop="version" label="版本" sortable>
            </el-table-column>
            <el-table-column prop="application" label="负载" sortable>
            </el-table-column>
            <el-table-column prop="ip" label="ip" sortable>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template scope="scope">
                    <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :page-size="20" :total="total" style="float:right;">
            </el-pagination>
        </el-col>

        <!--编辑界面-->
        <el-dialog title="编辑" v-model="editFormVisible" :close-on-click-modal="false">
            编辑页面
            <div slot="footer" class="dialog-footer">
                <el-button @click.native="editFormVisible = false">取消</el-button>
                <el-button type="primary" @click.native="editSubmit" :loading="editLoading">提交</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import util from '../../common/js/util'
    import { getUserListPage, removeUser, batchRemoveUser, editUser, addUser } from '../../api/api';

    export default {
        data() {
            return {
                filters: {
                    name: ''
                },
                DataSource: [
                ],   // 这是列表里的数据
                total: 0,
                page: 1,
                listLoading: false,

                editFormVisible: false,//编辑界面是否显示
                editLoading: false,
                editFormRules: {
                    name: [
                        { required: true, message: '请输入姓名', trigger: 'blur' }
                    ]
                },
                //编辑界面数据
                editForm: {
                    id: 0,
                    name: '',
                    sex: -1,
                    age: 0,
                    birth: '',
                    addr: ''
                },
            }
        },
        methods: {
          initWebSocket () {
            // 连接错误
            this.websocket.onerror = this.setErrorMessage

            // 连接成功
            this.websocket.onopen = this.setOnopenMessage

            // 收到消息的回调
            this.websocket.onmessage = this.setOnmessageMessage

            // 连接关闭的回调
            this.websocket.onclose = this.setOncloseMessage

            // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
            window.onbeforeunload = this.onbeforeunload
          },
          setErrorMessage () {
            console.log('WebSocket连接发生错误   状态码：' + this.websocket.readyState)
          },
          setOnopenMessage () {
            console.log('WebSocket连接成功    状态码：' + this.websocket.readyState)
          },
          setOnmessageMessage (event) {
            // 根据服务器推送的消息做自己的业务处理
            // console.log(event.data);
            let result = JSON.parse(event.data);
            this.total = result.length;
            // let temp = [];
            this.DataSource = [];
            for(let i=0; i<result.length; i++){
              let obj = {
                name: result[i].serviceName.match(/(\S*)version/)[1],
                group: result[i].group,
                version: result[i].serviceName.match(/version(\S*)/)[1],
                application: result[i].loadBalance,
                ip: result[i].ip
              }
              this.DataSource.push(obj)
            }

            console.log('服务端返回：' + result.toString())
          },
          setOncloseMessage () {
            console.log('WebSocket连接关闭    状态码：' + this.websocket.readyState)
          },
          onbeforeunload () {
            this.closeWebSocket()
          },
          closeWebSocket () {
            this.websocket.close()
          },
            handleCurrentChange(val) {
                this.page = val;
                this.getData();
            },
            //获取列表
            getData() {
                let para = {
                    page: this.page,
                    name: this.filters.name
                };
                this.listLoading = true;
                this.listLoading = false;


            },
            getSearchData() {
              // 搜索逻辑在这写
            },
          methods: {
            handleClickOutside() {
              this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
            }
            },
            //删除
            handleDel: function (index, row) {
                this.$confirm('确认删除该记录吗?', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    //NProgress.start();
                    let para = { id: row.id };
                    removeUser(para).then((res) => {
                        this.listLoading = false;
                        //NProgress.done();
                        this.$message({
                            message: '删除成功',
                            type: 'success'
                        });
                        this.getUsers();
                    });
                }).catch(() => {

                });
            },
            //显示编辑界面
            handleEdit: function (index, row) {
                this.editFormVisible = true;
                this.editForm = Object.assign({}, row);
            },
            //编辑
            editSubmit: function () {
                this.$refs.editForm.validate((valid) => {
                    if (valid) {
                        this.$confirm('确认提交吗？', '提示', {}).then(() => {
                            this.editLoading = true;
                            //NProgress.start();
                            let para = Object.assign({}, this.editForm);
                            para.birth = (!para.birth || para.birth == '') ? '' : util.formatDate.format(new Date(para.birth), 'yyyy-MM-dd');
                            editUser(para).then((res) => {
                                this.editLoading = false;
                                //NProgress.done();
                                this.$message({
                                    message: '提交成功',
                                    type: 'success'
                                });
                                this.$refs['editForm'].resetFields();
                                this.editFormVisible = false;
                                this.getUsers();
                            });
                        });
                    }
                });
            },
            // selsChange: function (sels) {
            //     this.sels = sels;
            // },
        },
        mounted() {
            this.getData();
          // WebSocket
          if ('WebSocket' in window) {
            this.websocket = new WebSocket('ws://localhost:8082/push/websocket')
            // alert('连接浏览器')
            this.initWebSocket()
          } else {
            alert('当前浏览器 不支持')
          }
        },
      beforeDestroy () {
        this.onbeforeunload()
        }
    }

</script>

<style scoped>

</style>
