(ns moon.impl.actor
  (:require [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(extend-type Actor
  actor/Actor
  (add-listener! [actor listener]
    (.addListener actor listener))

  (user-object [actor]
    (.getUserObject actor))

  (stage [actor]
    (.getStage actor))

  (set-position! [actor [x y]]
    (.setPosition actor x y))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (visible? [actor]
    (.isVisible actor))

  (parent [actor]
    (.getParent actor)))
