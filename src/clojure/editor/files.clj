(ns clojure.editor.files
  (:require [clojure.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/get-files app)))
