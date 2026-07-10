(ns clojure.editor.files
  (:require [gdl.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/get-files app)))
