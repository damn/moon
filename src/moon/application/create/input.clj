(ns moon.application.create.input)

(defn step [{:keys [ctx/app ctx/stage] :as ctx}]
  (.setInputProcessor (.getInput app) stage)
  (assoc ctx :ctx/input (.getInput app)))
