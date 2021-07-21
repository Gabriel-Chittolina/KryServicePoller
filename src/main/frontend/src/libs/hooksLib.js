import { useState, useEffect } from "react";

export function useFormFields(initialState) {
  const [fields, setValues] = useState(initialState);

  return [
    fields,
    function (event) {
      setValues({
        ...fields,
        [event.target.id]: event.target.value
      });
    }
  ];
}

export function useEffectOnce(func) {
  useEffect(() => {
    func();
  }, []);
}
