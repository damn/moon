(ns clojure.gdx.scene2d.actor.add-listener
  (:require [clojure.gdx.scene2d.listener :as listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn add-listener! [actor [listener-k listener-params]]
  (Actor/.addListener actor (listener/create [listener-k listener-params])))
