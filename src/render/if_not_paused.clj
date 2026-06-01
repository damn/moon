(ns render.if-not-paused)

(defn step
  [{:keys [ctx/paused?]
    :as ctx}
   stepfns]
  (if paused?
    ctx
    (reduce (fn [ctx f]
              (f ctx))
            ctx
            stepfns)))
