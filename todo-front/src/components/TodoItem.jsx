import React from 'react';

const TodoItem = ({todo, onToggle, onRemove }) => {
  let { id, code, name, status } = todo;
  let isActived = status ? 'todoItem active' : 'todoItem';
  return (
    <li className={isActived}>
      <div className="item">
        <input type="checkbox" id={code} checked={status} onChange={()=> onToggle(todo)} />
        <label htmlFor={code}></label>
        <span>{name}</span>
      </div>
      <div className="item">
        <button className="btn" onClick={() => onRemove(todo.id)}>삭제</button>
      </div>
    </li>
  );
};

export default TodoItem;
