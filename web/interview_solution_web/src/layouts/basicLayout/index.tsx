"use client"
import { GithubFilled, LogoutOutlined } from "@ant-design/icons";
import { Image } from 'antd';
import {
  ProConfigProvider,
  ProLayout,
  SettingDrawer,
} from '@ant-design/pro-components';
import {
  ConfigProvider,
  Dropdown,

} from 'antd';
import Link from "next/link";
import menus from "../../../config/menus";
import SearchInput from "@/layouts/basicLayout/components/SearchInput";
import { hidden } from "next/dist/lib/picocolors";
import GlobalFooter from "@/components/ GlobalFooter";

interface Props {
  children: React.ReactNode;
}
export default function BasicLayout({ children }: Props) {


  return (
    <div
      id="basic-layout"
      style={{
        height: '100vh',
        overflow: 'auto',
      }}
    >
      <ProConfigProvider hashed={false}>
        <
          ConfigProvider
        >
          <ProLayout
            prefixCls=""
            layout="top"

            title="面试解析"

            logo={
            <div style={{ borderRadius: "10px" ,width:"100%", height:"100%", overflow:"hidden" }}>
              <Image
                src="/assets/logo.png"
                alt="面试解析网站"
                width={60}
                preview={false}
                height={60 }
              />
            </div>

            }

            location={{
            }}

            siderMenuType="group"
            menu={{
              collapsedShowGroupTitle: true,
            }}

           // 头像

            avatarProps={{
              src: "/avatars/user.png",
              size: "small",
              title: "varin",
              render: (props, dom) => {
                return (
                  <Dropdown
                    menu={{
                      items: [
                        {
                          key: "logout",
                          icon: <LogoutOutlined />,
                          label: "退出登录",
                        },
                      ],
                    }}
                  >
                    {dom}
                  </Dropdown>
                );
              },
            }}

            // 标题渲染
            headerTitleRender={(logo, title, _) => {
              return (
                <a href="https://www.mianshiya.com" target="_blank">
                  {logo}
                  {title}
                </a>
              );

            }}

            // 操作渲染
            actionsRender={(props) => {
              if (props.isMobile) return [];
              return [
                <SearchInput key="search" />,
                <a
                  key="github"
                  href="https://github.com/liyupi/mianshiya-next"
                  target="_blank"
                >
                  <GithubFilled key="GithubFilled" />
                </a>,
              ];
            }}




            // 菜单项数据
            menuDataRender={() => {
              return menus
            }}

            // 菜单渲染
            menuItemRender={(item, dom) => (
              <Link href={item.path || "/"} target={item.target}>{dom}</Link>
            )}




            // 操作渲染



          >



            <SettingDrawer


            />
            {children}

          </ProLayout>

        </ConfigProvider>
      </ProConfigProvider>

      <GlobalFooter/>
    </div>
  );
};


