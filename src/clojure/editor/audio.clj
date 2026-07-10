(ns clojure.editor.audio
  (:require [gdl.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (application/get-audio app)))
