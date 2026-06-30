(ns editor.widget-value.one-to-one
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [_  widget _schemas]
  (->> (gdx/get-children widget)
       (keep Actor/.getUserObject)
       first))
