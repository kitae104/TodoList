import React from 'react'

const TodoInput = ({onAdd}) => {
  return (
    <div>
      <form action="" className='form'>
        <input type='text' 
          placeholder='할 일 입력'
          className='input'   
                  
        />
        <button type='submit' className='btn' onClick={onAdd}>추가</button>
      </form>
    </div>
  )
}

export default TodoInput