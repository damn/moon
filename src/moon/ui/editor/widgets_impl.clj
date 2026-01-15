(ns moon.ui.editor.widgets-impl
  (:require [clojure.edn :as edn]
            [moon.schema :as schema]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               TextField
                                               TextTooltip)))

(defmethod schema/create :default
  [_ v {:keys [^Skin ctx/skin]}]
  (Label. (utils/truncate (utils/->edn-str v) 60) skin))

(defmethod schema/value :default
  [_  widget _schemas]
  ((.getUserObject widget) 1))

(defn- create-edn-widget [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (utils/->edn-str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defn- edn-widget-value [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))

(def fn-map
  {:s/number {schema/create  create-edn-widget
              schema/value   edn-widget-value}
   :s/val-max {schema/create create-edn-widget
               schema/value  edn-widget-value}})

(doseq [[schema-k impls] fn-map
        [multifn method-var] impls]
  (clojure.lang.MultiFn/.addMethod multifn schema-k method-var))
