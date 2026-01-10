(ns gdl.ui.stage
  (:import (moon Stage)))

(defmulti build :type)

(defn create [viewport batch]
  (Stage. viewport batch))

(defn ctx
  "Returns the context object associated with the stage. See [[set-ctx!]]."
  [^Stage stage]
  (.ctx stage))

(defn set-ctx!
  "Sets a context object, which can be accessed via [[ctx]]. Useful so callback functions of actors can get state passed via this context object instead of having to access global state."
  [^Stage stage ctx]
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
