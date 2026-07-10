(ns clojure.editor.input
  (:require [com.badlogic.gdx.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (application/getInput app)))
