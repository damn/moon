(ns clojure.editor.graphics)

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (.getGraphics app)))
