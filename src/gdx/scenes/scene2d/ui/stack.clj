(ns gdx.scenes.scene2d.ui.stack
  (:require [clojure.gdx.scene2d.ui.stack :as stack]
            [gdx.scenes.scene2d.group :as group]))

(defn create
  [opts]
  (doto (stack/create)
    (group/set-opts! opts)))
