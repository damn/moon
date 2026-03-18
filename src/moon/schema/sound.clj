(ns moon.schema.sound
  (:require [moon.actor :as actor]
            [moon.audio :as audio]
            [moon.scroll-pane-cell :as scroll-pane-cell]
            [moon.table :as table]
            [moon.text-button :as text-button]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Table
                                               Window)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn malli-form [_ _schemas]
  :string)

(declare sound-columns)

(defn- rebuild-sound-widget! [^Table table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (.clearChildren table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (Actor/.remove (actor/find-ancestor actor Window))
    (Window/.pack (actor/find-ancestor table Window))
    (let [[k _] (Actor/.getUserObject table)]
      (.setUserObject table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [_actor {:keys [ctx/audio
                      ctx/skin
                      ctx/stage]}]
    (Stage/.addActor stage
                     (doto (Window. "Choose" ^Skin skin)
                       (window/add-close-button! skin)
                       (table/set-opts! {:rows [[(scroll-pane-cell/create skin
                                                                          (Viewport/.getWorldWidth (Stage/.getViewport stage))
                                                                          (for [sound-name (audio/sound-names audio)]
                                                                            [{:actor (text-button/create
                                                                                      {:text sound-name
                                                                                       :on-clicked (rebuild-sound-widget! table sound-name)
                                                                                       :skin skin})}
                                                                             {:actor (text-button/create
                                                                                      {:text "play!"
                                                                                       :on-clicked (fn [_actor {:keys [ctx/audio]}]
                                                                                                     (audio/play! audio sound-name))
                                                                                       :skin skin})}]))]]})
                       (.setModal true)
                       (.pack)))))

(defn- sound-columns [skin table sound-name]
  [{:actor (text-button/create
            {:text sound-name
             :on-clicked (open-select-sounds-handler table)
             :skin skin})}
   {:actor (text-button/create
            {:text "play!"
             :on-clicked (fn [_actor {:keys [ctx/audio]}]
                           (audio/play! audio sound-name))
             :skin skin})}])


(defn create [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create {:cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor (text-button/create
                                        {:text "No sound"
                                         :on-clicked (open-select-sounds-handler table)
                                         :skin skin})}])])
    table))
