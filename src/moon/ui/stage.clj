(ns moon.ui.stage
  (:import (moon Stage)))

(defmulti build :type)

(defn create [viewport batch]
  (Stage. viewport batch))

(defn ctx [^Stage stage]
  (.ctx stage))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))

(defn root [^Stage stage]
  (.getRoot stage))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn hit [^Stage stage [x y] touchable?]
  (.hit stage x y touchable?))

(defn clear! [^Stage stage]
  (.clear stage))
