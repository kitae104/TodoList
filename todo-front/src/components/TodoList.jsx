import React from 'react'
import TodoItem from './TodoItem'

const TodoList = ({todoList, onToggle, onRemove, onAdd}) => {
  return (
    <ul className='todoList'>
      {
        todoList.map(todo => {
          return <TodoItem 
            key={todo.id} 
            todo={todo} 
            onToggle={onToggle}
            onRemove={onRemove}
            onAdd={onAdd}
          />
        })
      }
      
    </ul>
  )
}

export default TodoList