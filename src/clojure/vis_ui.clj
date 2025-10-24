(ns clojure.vis-ui
  (:import (com.kotcrab.vis.ui VisUI
                               VisUI$SkinScale)))

(defn loaded? []
  (VisUI/isLoaded))

(defn dispose! []
  (VisUI/dispose))

(defn load! [skin-scale]
  (VisUI/load (case skin-scale
                :x1 VisUI$SkinScale/X1
                :x2 VisUI$SkinScale/X2)))

(defn skin []
  (VisUI/getSkin))
