(ns gdl.shapedrawer.shape-drawer
  (:refer-clojure :exclude [new])
  (:require [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]))

(defn circle! [& args]
  (apply shape-drawer/circle args))

(defn ellipse! [& args]
  (apply shape-drawer/ellipse args))

(defn filled-circle! [& args]
  (apply shape-drawer/filledCircle args))

(defn filled-rectangle! [& args]
  (apply shape-drawer/filledRectangle args))

(defn get-default-line-width [& args]
  (apply shape-drawer/getDefaultLineWidth args))

(defn line! [& args]
  (apply shape-drawer/line args))

(defn new [& args]
  (apply shape-drawer/new args))

(defn rectangle! [& args]
  (apply shape-drawer/rectangle args))

(defn sector! [& args]
  (apply shape-drawer/sector args))

(defn set-color! [& args]
  (apply shape-drawer/setColor args))

(defn set-default-line-width! [& args]
  (apply shape-drawer/setDefaultLineWidth args))
