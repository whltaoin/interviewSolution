import "./index.css"

export default function GlobalFooter() {

  const currentYear = new Date().getFullYear()
  return(
    <div className="global-footer">
      <div>© {currentYear} 面试解析平台</div>
      <div>
        <a href="http://www.varin.cn" target="_blank">
          作者：varin.cn - varin
        </a>
      </div>

    </div>
  )
}