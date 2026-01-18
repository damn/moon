(ns moon.start.add-methods
  (:import (clojure.lang MultiFn)))

(defn- add-methods! [multifn-vars ns-sym k & {:keys [optional?]}]
  (doseq [multifn-var multifn-vars
          :let [_ (assert (var? multifn-var))
                function-sym (symbol (str ns-sym "/" (:name (meta multifn-var))))
                function-var (resolve function-sym)]]
    (assert (or optional? function-var)
            (str "Cannot find required `" (:name (meta multifn-var)) "` function in " ns-sym " - function-sym: " function-sym))
    (when function-var
      (assert (keyword? k))
      (assert (var? function-var) (pr-str function-var))
      (let [multifn @multifn-var]
        (when (k (methods multifn))
          (println "WARNING: Overwriting function" (:name (meta function-var)) "on" k))
        #_(println "MultiFn/.addMethod " multifn-var  k function-var)
        (MultiFn/.addMethod multifn k function-var)))))

(defn step [ctx {:keys [required optional]} components]
  (doseq [[ns-sym k] components]
    (require ns-sym)
    (add-methods! required ns-sym k)
    (add-methods! optional ns-sym k :optional? true))
  ctx)
