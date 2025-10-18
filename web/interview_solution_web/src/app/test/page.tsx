"use client"
import { Image } from 'antd';
export default function TestPage() {
  return (
    <div id={"test-page"} >
      <h1>测试</h1>

      <Image
        width={200}
        src="/avatars/user.png"
      />
    </div>
  );
}