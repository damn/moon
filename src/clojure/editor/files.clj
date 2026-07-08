(ns clojure.editor.files)

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (.getFiles app)))
