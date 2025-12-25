(ns moon.application.create.graphics)

(defn step
  [{:keys [ctx/graphics
           ctx/files] :as ctx}
   {:keys [impl config]}]
  (assoc ctx :ctx/graphics (impl graphics files config)))
