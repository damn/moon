(ns editor.widget-value.enum
  (:require [clojure.edn :as edn]
            [scene2d.ui.select-box.get-selected :as get-selected]))

(defn f [_  widget _schemas]
  (edn/read-string (get-selected/f widget)))
