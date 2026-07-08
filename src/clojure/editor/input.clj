(ns clojure.editor.input)

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (.getInput app)))
