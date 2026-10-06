(ns game.listener
  (:require [game.listener.create :refer [create!]]
            [game.listener.dispose :refer [dispose!]]
            [game.listener.render :refer [render!]]
            [game.listener.resize :refer [resize!]])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input)))

(def listener
  (reify ApplicationListener
    (create [_]
      (create! Gdx/audio Gdx/files Gdx/input))

    (dispose [_]
      (dispose!))

    (render [_]
      (let [input Gdx/input]
        (render! #(.isKeyPressed ^Input input (int %))
                 #(.isKeyJustPressed ^Input input (int %))
                 #(.isButtonJustPressed ^Input input (int %)))))

    (resize [_ width height]
      (resize! width height))

    (pause [_])

    (resume [_])))
