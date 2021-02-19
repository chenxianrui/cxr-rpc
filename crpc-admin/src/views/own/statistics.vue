<template>
    <section>
        <!--工具条-->
        <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
            <el-form :inline="true" :model="filters">
                <el-form-item>
                    <el-button type="primary" v-on:click="provide">提供者</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" v-on:click="consume">消费者</el-button>
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
            <el-table-column prop="application" label="ip" sortable>
            </el-table-column>
            <el-table-column prop="ip" label="负载" sortable>
            </el-table-column>
            <!--<el-table-column label="操作" width="150">-->
                <!--<template scope="scope">-->
                    <!--<el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>-->
                    <!--<el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>-->
                <!--</template>-->
            <!--</el-table-column>-->
        </el-table>

        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <!--<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0">批量删除</el-button>-->
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
    export default {
        name: "statistics",
        methods: {
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
            provide(){
                //获取提供者的代码
            },
            consume(){
                //获取消费者的代码
            },
        },
    }
</script>

<style scoped>

</style>
