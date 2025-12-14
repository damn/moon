(ns moon.application.create.ui)

(defn step
  [{:keys [ctx/graphics] :as ctx}
   {:keys [impl config]}]
  (assoc ctx :ctx/stage (impl graphics config)))
