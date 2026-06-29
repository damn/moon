(ns editor.widget-value.enum
  (:require [clojure.edn :as edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn f [_  widget _schemas]
  (edn/read-string (SelectBox/.getSelected widget)))
