(ns clojure.editor.files
  (:require [com.badlogic.gdx.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/getFiles app)))
