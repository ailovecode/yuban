import { createApp } from 'vue'
import Vant from 'vant';
import 'vant/lib/index.css';
import * as VueRouter from 'vue-router';
import routes from "./config/router.ts";
import App from "./App.vue";
import 'vant/es/toast/style'
import 'vant/es/dialog/style'
import { Toast, Dialog } from 'vant'
import { createPinia } from 'pinia'


const app = createApp(App);
const pinia = createPinia();
app.use(Vant);
app.use(Toast);
app.use(Dialog);
app.use(pinia);

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

app.use(router);

app.mount('#app');