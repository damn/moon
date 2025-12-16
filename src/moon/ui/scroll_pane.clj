(ns moon.ui.scroll-pane
  (:require [gdl.ui.scroll-pane :as scroll-pane]
            [moon.ui :as ui]))

(defn create [actor]
  (scroll-pane/create actor ui/skin))
