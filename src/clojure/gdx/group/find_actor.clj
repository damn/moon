(ns clojure.gdx.group.find-actor
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [^Group group name]
  (Group/.findActor group name))
