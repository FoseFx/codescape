
/**
 * Name: In Darth Mauls Fußstapfen
 * Batch: 2
 * Level: 3
 * Instructions used: 1
 * 
 * Copyright (c) 2020 Max Baumann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import codescape.Dogbot;

public class MyDogbot extends Dogbot {

    public void run() {

        String WORLD_URL = "http://" + System.getenv("WORLD") + "/";
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        for (int i = 0; i < 10; i++) {
            String r = "";
            for (int j = 0; j < 2; j++) {
                String instr;
                if (j == 0) {
                    instr = "isMovePossible";
                } else if (r.equals("true")) {
                    instr = "move";
                } else {
                    instr = "rest";
                }
                r = mdoRequest(instr, "", client, WORLD_URL);
            }
        }
    }

    private static String mdoRequest(final String target, final String body, final okhttp3.OkHttpClient client,
            final String WORLD_URL) {
        try {
            final okhttp3.Request request = new okhttp3.Request.Builder().url(WORLD_URL + target)
                    .post(okhttp3.RequestBody.create(body, okhttp3.MediaType.get("text/plain; charset=utf-8"))).build();
            final okhttp3.Response response = client.newCall(request).execute();
            final String responseBody = (response.body() != null) ? response.body().string() : "";
            if (response.code() == 410) {
                Thread.currentThread().stop();
            }
            return responseBody;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}