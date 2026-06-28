(ns scene2d.actor.add-listener
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn add-listener! [^Actor actor listener]
  (.addListener actor listener))
