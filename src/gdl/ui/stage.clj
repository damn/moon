(ns gdl.ui.stage
  (:import (gdl.ui Stage)))

(defmulti build :type)

(defn create [viewport batch config]
  (Stage. viewport batch config))

(defn ctx [^Stage stage]
  (.ctx stage))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn act! [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))

(defn add-actor! [^Stage stage actor-decl]
  (.addActor stage (build actor-decl)))

(defn root [^Stage stage]
  (.getRoot stage))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn hit [^Stage stage [x y] touchable?]
  (.hit stage x y touchable?))

(defn clear! [^Stage stage]
  (.clear stage))
