(ns clojure.editor.audio
  (:require [clojure.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (application/get-audio app)))
