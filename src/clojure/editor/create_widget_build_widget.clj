(ns clojure.editor.create-widget-build-widget
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget :refer [create-widget]]))

(defn build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (actor/setUserObject widget [k v])
    widget))
