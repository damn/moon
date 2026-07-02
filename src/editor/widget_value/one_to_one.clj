(ns editor.widget-value.one-to-one
  (:require [clojure.gdx.actor.get-user-object :as get-user-object])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [_  widget _schemas]
  (->> (Group/.getChildren widget)
       (keep get-user-object/f)
       first))
