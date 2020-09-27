(ns operations.smart
  (:require [nodes.struct :as s]
            [nodes.relations :as rel]
            [operations.checked :as checked]))
;; Smart operations exist of multiple checked and smooth operations.
;; If not it should be in checked or smooth operations.

;;;;;;;;;;;;;;;;;;;First smart expression => EXTRACT FUNCTION;;;;;;;;;;;;;;;
;; expression can be a
;; func call, a func def, constant, parameter
;; var a = 3
;; var a = newFunc();
;; var newFunc(){ return 3;}
;;Extract func def
;; var a(){5};     =>
;; var a = newFunc();
;; var newFunc(){return (){5};}
;;parameter?
;; a(b){b+1};      =>
;; a(b){c(b)+1};
;; var c(b){return b};
;; Nested function
;; a(c)-> b() -> c + 3
;; => a(c) -> b() -> (d()->c + 3)()       STEP 1: replace with function call and function def
;; => a(c) -> d()-> c + 3, b() -> d()     STEP 2: Move function up
;; => d(e)-> e + 3, a(c) -> b() -> d(c)   STEP 3: Move function up

(defn replace-with-direct-function-call [struct expression new-function-name] ;;Function call resided at the same level
  (let [parent-scope (rel/get-scope-parent struct expression)
        function-add (checked/create-func struct new-function-name parent-scope [])
        function-id (checked/get-node-result function-add)
        function-add-result (checked/get-struct-result function-add)
        function-call-add (checked/create-function-call function-add-result function-id parent-scope [])
        function-call-id (checked/get-node-result function-call-add)
        function-call-result (checked/get-struct-result function-call-add)
        function-result-set (checked/set-as-function-result function-call-result function-id expression)
        function-result-set-result (checked/get-struct-result function-result-set)
        replaced (checked/replace-all-usages-with function-result-set-result expression function-call-id)]
    replaced))
(defn move-function-up [struct expression])
;;find everything that is pointing outwards,
;; take all parameter usages from the current scope, and for all these parameters, create new parameters.
;; Rebind all these old parameter usages to new parameter usages


(defn extract-function
  [struct expression new-parentscope]

  )