import { createApp } from 'vue'
import Vant from 'vant';
import 'vant/lib/index.css';
import * as VueRouter from 'vue-router';
import routes from "./config/router.ts";
import App from "./App.vue";


const app = createApp(App);
app.use(Vant);

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

app.use(router);

app.mount('#app');