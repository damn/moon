(ns com.badlogic.gdx.scenes.scene2d.event
  (:require [clojure.scene2d.event :as event])
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(extend-type Event
  event/Event
  (stage [event]
    (.getStage event)))
