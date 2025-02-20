import React, { useEffect, useState } from 'react'
import TodoHeader from './TodoHeader'
import TodoInput from './TodoInput'
import TodoList from './TodoList'
import TodoFooter from './TodoFooter'

const TodoContainer = () => {

  // state 
  const [todoList, setTodoList] = useState([])
  const [input, setInput] = useState('')

  // event handler
  // 자식에서 처리한 이벤트를 부모에게 전달하기 위한 함수 -> props로 자식으로 전달
  const onAdd = async (e) => {
    e.preventDefault()  // submit 이벤트의 기본 동작을 막음
    console.log('데이터 추가 : ' + input)

    const data = {
      name: input === '' ? '제목없음' : input,  
      status: 0,   
      seq: 1
    }

    const options = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }

    try{
      const url = 'http://localhost:8080/todo'
      const response = await fetch(url, options)
      const msg = await response.text()
      console.log(`message : ${msg}`)
    } catch(err) {
      console.error(err)
    }    
    getList()    // 서버로 부터 할일 목록을 다시 받아옴
    setInput('')  // 입력창 초기화 
  }

  // 입력창에 입력한 내용을 state에 저장
  const onChange = (e) => {
    setInput(e.target.value)
    // console.log(e.target.value)  // 입력창에 입력한 내용(체크용)
  }

  const onRemove = async (id) => {
    console.log('delete : ' + id)

    const options = {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },      
    }

    try{
      const url = `http://localhost:8080/todo/${id}`
      const response = await fetch(url, options)
      const msg = await response.text()
      console.log(`message : ${msg}`)
    } catch(err) {
      console.error(err)
    }     
    getList()    // 서버로 부터 할일 목록을 다시 받아옴
  }
  

  const onToggle = async (todo) => {    
    // 상태 수정 요청 
    const data = {
      ...todo,
      status: !todo.status      
    }
    const options = {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }
    
    try{
      const url = 'http://localhost:8080/todo'
      const response = await fetch(url, options)
      const msg = await response.text()
      console.log(`message : ${msg}`)
    } catch(err) {
      console.error(err)
    }     
    getList()    // 서버로 부터 할일 목록을 다시 받아옴
  }

  const getList = () => {
    // 할일 목록 요청 
    fetch('http://localhost:8080/todo')
      .then(res => res.json())
      .then(data => {        
        setTodoList(data.content)
      })
      .catch(err => {
        console.error(err)
      })    
  }

  useEffect(() => {
    getList()
  }, [])

  return (
    <div className='container'>
      <TodoHeader />
      <TodoInput input={input} onChange={onChange} onAdd={onAdd} />
      <TodoList todoList = { todoList } onToggle={onToggle} onRemove={onRemove}/>
      <TodoFooter />
    </div>
  )
}

export default TodoContainer