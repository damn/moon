(ns clojure.gdx.scenes.scene2d.ui.horizontal-group
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create
  [{:keys [space pad] :as opts}]
  (doto (HorizontalGroup.)
    (.space space)
    (.pad pad)
    (actor/set-opts! opts)))
