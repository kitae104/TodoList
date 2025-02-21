import React, { use, useEffect, useState } from 'react';
import TodoItem from './TodoItem';

const TodoList = ({ todoList, onToggle, onRemove }) => {
  // 스크롤 이벤트 처리를 위한 state
  const [page, setPage] = useState(0);
  const [newList, setNewList] = useState([]);
  
  // 다음 페이지 데이터를 가져오는 함수
  const addList = (page) => {
    fetch(`http://localhost:8080/todo?page=${page}`)
      .then((response) => response.json())
      .then((data) => {
        console.log(data);

        // 마지막 페이지 여부 확인 
        if( page > data.totalPages) {
          alert('마지막 페이지입니다.')
          return 
        }

        const newTodoList = [ ...newList, ...data.content ]
        setNewList(newTodoList)
        setPage(page)
      })
      .catch((err) => {
        console.error(err);
      });
  };

  // 스크롤 이벤트 처리
  const handleScroll = () => {
    const todoListElement = document.querySelector('.todoList')
    const scrollHeight = todoListElement.scrollHeight  // 스크롤 높이
    const scrollTop = todoListElement.scrollTop        // 스크롤 위치
    const clientHeight = todoListElement.clientHeight  // 컨텐츠 높이
    
    if (Math.abs(scrollHeight - scrollTop - clientHeight) < 1) {
      // 다음 페이지 데이터를 가져옴
      addList(page + 1);
    }
  };

  useEffect(() => {
    setNewList(todoList)
  }, [todoList]);

  useEffect(() => {
    // 스크롤 이벤트 등록
    const todoListElement = document.querySelector('.todoList')
    if( todoListElement ) {
      todoListElement.addEventListener('scroll', handleScroll)
    }
    // 스크롤 이벤트 제거
    return () => {
      if( todoListElement ) {
        todoListElement.removeEventListener('scroll', handleScroll)
      }
    }
  });

  return (
    <div>
      <ul className="todoList">
        {newList.map((todo) => (
            <TodoItem
              key={todo.id}
              todo={todo}
              onToggle={onToggle}
              onRemove={onRemove}
            />
          ))
        }
      </ul>      
    </div>  
  );
};

export default TodoList;
