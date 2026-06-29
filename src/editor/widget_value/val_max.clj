(ns editor.widget-value.val-max
  (:require [clojure.edn :as edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn f
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
