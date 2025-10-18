export default function BlogIdPage({params}:{
  params:{
    id:string
  }
}){
  return (
    <div>
      <h1>URL中ID的值为：{params.id}</h1>
    </div>
  )
}