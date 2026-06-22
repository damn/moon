(ns gdl.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)))

(defprotocol P
  (get-stage [_]))

(extend-type Actor
  P
  (get-stage [actor]
    (.getStage actor)))

(extend-type Event
  P
  (get-stage [event]
    (.getStage event)))
