(ns gdx.scenes.scene2d.ui.stack
  (:require [clojure.gdx.scene2d.ui.stack :as stack]
            [clojure.gdx.scene2d.group.set-opts :refer [set-opts!]]))

(defn create
  [opts]
  (doto (stack/create)
    (set-opts! opts)))
