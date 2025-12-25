(ns moon.application.create.input)

(defn step [{:keys [ctx/input
                    ctx/stage] :as ctx}]
  (.setInputProcessor input stage)
  (assoc ctx :ctx/input input))
