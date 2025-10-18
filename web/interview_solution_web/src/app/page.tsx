  import Image from "next/image";
  import styles from "./page.module.css";
  import { Button } from "antd";

  export default function Home() {
    return (
     <div id="home">
       <span>首页</span>
       <Button type={"primary"}>点击一下</Button>
     </div>
    );
  }
