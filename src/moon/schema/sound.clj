(ns moon.schema.sound
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.audio :as audio]
            [moon.schema :as schema]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window]
            [moon.ui.scroll-pane-window :as scroll-pane-window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)))

(defmethod schema/malli-form :s/sound [_ _schemas]
  :string)

(declare sound-columns)

(defn- rebuild-sound-widget! [table sound-name]
  (fn [actor {:keys [ctx/skin]}]
    (.clearChildren table)
    (table/add-rows! table [(sound-columns skin table sound-name)])
    (.remove (window/find-ancestor actor))
    (.pack (window/find-ancestor table))
    (let [[k _] (Actor/.getUserObject table)]
      (.setUserObject table [k sound-name]))))

(defn- open-select-sounds-handler [table]
  (fn [_actor {:keys [ctx/audio
                      ctx/skin
                      ctx/stage]}]
    (Stage/.addActor stage
                      (scroll-pane-window/create
                       {:skin skin
                        :viewport-height (.getWorldWidth (.getViewport stage))
                        :rows (for [sound-name (audio/sound-names audio)]
                                [{:actor (text-button/create
                                          {:text sound-name
                                           :on-clicked (rebuild-sound-widget! table sound-name)
                                           :skin skin})}
                                 {:actor (text-button/create
                                          {:text "play!"
                                           :on-clicked (fn [_actor {:keys [ctx/audio]}]
                                                         (audio/play! audio sound-name))
                                           :skin skin})}])}))))

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


(defmethod schema/create :s/sound [_  sound-name {:keys [ctx/skin]}]
  (let [table (table/create {:cell-defaults {:pad 5}})]
    (table/add-rows! table [(if sound-name
                              (sound-columns skin table sound-name)
                              [{:actor (text-button/create
                                        {:text "No sound"
                                         :on-clicked (open-select-sounds-handler table)
                                         :skin skin})}])])
    table))
