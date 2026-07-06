(ns editor.widget-value.enum
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box] [clojure.edn :as edn]))

(defn f [_  widget _schemas]
  (edn/read-string (select-box/get-selected widget)))
